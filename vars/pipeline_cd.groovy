/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
  	// caso pilepile-cd -> ■ 'downloadNexus','runDownloadedJar','rest','nexusCD'
	stage('downloadNexus') {
		// sh ".gladlew clean build"
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('downloadNexus') || env.PARAM_STAGE.isEmpty()) {
			sh 'curl -o DevOpsUsach2020-0.0.1.jar http://localhost:8082/repository/test-repo/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar'
		}
		else {
			println "no ejecutar stage downloadNexus"
		}
	}
	stage('runDownloadedJar') {
		// configurado en sonarcube-configuration
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('runDownloadedJar') || env.PARAM_STAGE.isEmpty()) {
			sh 'gradle bootRun &'
			sleep 30
		}
		else {
			println "no ejecutar stage runDownloadedJar"
		}
	}

	stage('rest') {
		//
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('rest') || env.PARAM_STAGE.isEmpty()) {
			sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'
		}
		else {
			println "no ejecutar stage rest"
		}

	}
	stage('nexusCD') {
		//
		env.ETAPA = env.STAGE_NAME
		if (env.PARAM_STAGE.contains('nexusCD') || env.PARAM_STAGE.isEmpty()) {
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
		else {
			println "no ejecutar stage nexusCD"
		}	
	}
}

return this; 
