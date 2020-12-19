 
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

	stage('Compile') {
		// 
		sh 'mvn clean compile -e'		
	}

	stage('Test') {
		// 
		sh 'mvn clean test -e'
	}
  
	stage('Build') {
		// 
		sh 'mvn clean package -e'
	}
	stage('Sonar') {
		// configurado en sonarcube-configuration
		def scannerHome = tool 'sonar-scanner';
		
		// conf generales
		withSonarQubeEnv('sonar-server') { 
			//sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
			bat "${scannerHome}\\bin\\sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
		}
	}
	stage('Run') {
		// 
		sh 'mvn spring-boot:run &'
		sleep 20
	}
	stage('Test') {
		//
		sh 'curl -X GET http://localhost:8081/rest/mscovid/test?msg=testing'

	}
	stage('Nexus') {
		//
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
