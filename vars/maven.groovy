import pipeline.utils.*
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(stage_param, branch_name){
    
    def validator = new Validator()

    flow_name = validator.getNameFlow(branch_name)

    bat 'set'

    repo_name = env.GIT_URL.split("/").last().replaceAll('.git', '')

    figlet flow_name

    println "DEBUG: stage_param: " + stage_param

    println "DEBUG: branch_name: " + branch_name

    println "DEBUG: env.GIT_URL: " + env.GIT_URL

    println "DEBUG: repo_name: " + repo_name


    //separamos los flujos CI/CD

    switch(flow_name) {
        case "Integracion Continua":
            ciFlow(stage_param)
        break;
        case "Despliegue Continuo":
            cdFlow(stage_param)
        break;
    }

    
}

// Flujo CI
def ciFlow(stage_param){

    def validator = new Validator()

    println "DEBUG: ciFlow..."


    if(validator.isValidStage('compile', stage_param)){
        stage('compile') {
            env.STAGE = STAGE_NAME
            sh './mvnw clean compile -e'   

        }
    }

    if(validator.isValidStage('unitTest', stage_param)){
        stage('unitTest') {
            env.STAGE = STAGE_NAME
            sh './mvnw clean test -e'   

        }
    }

    if(validator.isValidStage('jar', stage_param)){
        stage('jar') {
            env.STAGE = STAGE_NAME
            sh './mvnw clean package -e'   

        }
    }

    if(validator.isValidStage('sonar', stage_param)){
        stage('sonar') {
            env.STAGE = STAGE_NAME
            def scannerHome = tool 'sonar-scanner';
            withSonarQubeEnv('sonar-server') { 
                //sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                bat "${scannerHome}\\bin\\sonar-scanner -Dsonar.projectKey=${repo_name}-${branch_name}-${BUILD_NUMBER}-${EXECUTOR_NUMBER} -Dsonar.java.binaries=build"
            }
        }
    }

    if(validator.isValidStage('nexusUpload', stage_param)){ 
        stage('nexusUpload') {
            env.STAGE = STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus', 
               nexusRepositoryId: 'test-repo',
               packages: [
                   [$class: 'MavenPackage', mavenAssetList: [
                       [
                           classifier: '', 
                           extension: 'jar', 
                           filePath: 'C:\\Users\\jibanez\\.jenkins\\workspace\\emplo-gradle_feature-dir-inicial\\build\\libs\\DevOpsUsach2020-0.0.1.jar'
                        ]
                    ], 
                    mavenCoordinate: [
                        artifactId: 'DevOpsUsach2020', 
                        groupId: 'com.devopsusach2020', 
                        packaging: 'jar', 
                        version: '1.0.0'
                        ]
                    ]
                ]

        }
    }

    // def gitCreateRelease(String release) 
    if(validator.isValidStage('gitCreateRelease', stage_param) && env.GIT_BRANCH.contains('develop')){
        stage('gitCreateRelease') {
            env.STAGE = STAGE_NAME
            def git = new GitMethods()

            version = "1-1-1"

            if (git.checkIfBranchExists('release-v' + version)) {
                println "INFO: La rama existe"
                git.deleteBranch('release-v' + version) 
                println "INFO: Rala eliminada"
                git.createBranch(env.GIT_BRANCH, 'release-v' + version)
                println "INFO: Rama creada satisfactoriamente"
            }
            else {
                git.createBranch(env.GIT_BRANCH, 'release-v' + version)
                println "INFO: Rama creada satisfactoriamente"
            }
        }
    }
}

// Flujo CD
def cdFlow(stage_param){

    def validator = new Validator()

    if(validator.isValidStage('downloadNexus', stage_param)){
        stage('downloadNexus') {
            env.STAGE = STAGE_NAME
            sh 'curl -o DevOpsUsach2020-0.0.1.jar http://localhost:8082/repository/test-repo/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar'

        }
    }

    if(validator.isValidStage('runDownloadedNexus', stage_param)){
        stage('runDownloadedNexus') {
            env.STAGE = STAGE_NAME
            sh 'nohup mvn spring-boot:run &'
            sleep 30
            
        }
    }

    stage('rest') {
        env.STAGE = STAGE_NAME
        sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing' 

    }

    stage('nexusCD') {
        env.STAGE = STAGE_NAME
        // nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'taller-10-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/Users/procco/personal/usach/Modulo3/repositorios/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '1.0.0']]] 

        nexusPublisher nexusInstanceId: 'nexus', 
           nexusRepositoryId: 'test-repo',
           packages: [
               [$class: 'MavenPackage', mavenAssetList: [
                   [
                       classifier: '', 
                       extension: 'jar', 
                       filePath: 'C:\\Users\\jibanez\\.jenkins\\workspace\\emplo-gradle_feature-dir-inicial\\build\\libs\\DevOpsUsach2020-0.0.1.jar'
                    ]
                ], 
                mavenCoordinate: [
                    artifactId: 'DevOpsUsach2020', 
                    groupId: 'com.devopsusach2020', 
                    packaging: 'jar', 
                    version: '1.0.0'
                    ]
                ]
            ]
    
    }

}

return this;