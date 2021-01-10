import pipeline.utils.*
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(stage_param, branch_name){
    
    def validator = new Validator()

    flow_name = validator.getNameFlow(branch_name)

    figlet flow_name

    //separamos los flujos CI/CD

    switch(flow_name.toLowerCase()) {
        case "integracion continua":
            ciFlow(stage_param)
        break;
        case "despliegue continuo":
            cdFlow(stage_param)
        break;
    }

    
}

// Flujo CI
def ciFlow(stage_param){

    def validator = new Validator()

    if(validator.isValidStage('compile', stage_param)){
        stage('compile') {
            env.STAGE = STAGE_NAME
            sh './mvnw clean compile -e'   

        }
    }

    if(validator.isValidStage('test', stage_param)){
        stage('test') {
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
            withSonarQubeEnv(installationName: 'sonar_server') {
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
            }

        }
    }

    if(validator.isValidStage('nexusCI', stage_param)){ 
        stage('nexusCI') {
            env.STAGE = STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'taller-10-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/Users/procco/personal/usach/Modulo3/repositorios/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]] 

        }
    }

}

// Flujo CD
def cdFlow(stage_param){

    def validator = new Validator()

    if(validator.isValidStage('downloadNexus', stage_param)){
        stage('downloadNexus') {
            env.STAGE = STAGE_NAME
            sh "curl -X GET -u admin:procco2020 http://bf9c05ea07fd.ngrok.io/repository/taller-10-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"

        }
    }

    if(validator.isValidStage('runDownloadedNexus', stage_param)){
        stage('runDownloadedNexus') {
            env.STAGE = STAGE_NAME
            sh 'nohup mvn spring-boot:run &'
            
        }
    }

    stage('rest') {
        env.STAGE = STAGE_NAME
        sleep 20
        sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'" 

    }

    stage('nexusCD') {
        env.STAGE = STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'taller-10-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/Users/procco/personal/usach/Modulo3/repositorios/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '1.0.0']]] 
    
    }

}

return this;