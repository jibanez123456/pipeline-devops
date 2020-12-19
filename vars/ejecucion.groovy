def call(){

	pipeline {
    agent any

	parameters { choice(name: 'tool', choices: ['gradle', 'maven'], description: 'Selección herramienta de Construcción') }
    
    stages {
        stage('Pipeline') {
            steps {
                script {

                	env.ETAPA = ''
				
					params.tool

					if (params.tool == 'gradle') { 
							def ejecucion = load 'gradle.groovy'
							ejecucion.call()
					}
					else {
							def ejecucion = load 'maven.groovy'
							ejecucion.call()
					}
				}
            }
        }
    }	

    post {

    	success {
    		 slackSend message: '[Jose Ibanez] [' + env.JOB_NAME + '] [' + params.tool  + '] [Ejecucion exitosa] ', teamDomain: 'devops-usach-2020', tokenCredentialId: 'slacktoken'
    	}

    	failure {
    		 slackSend message: '[Jose Ibanez] [' + env.JOB_NAME + '] [' + params.tool  + '] [Ejecucion fallida en stage:' + env.ETAPA + '] ', teamDomain: 'devops-usach-2020', tokenCredentialId: 'slacktoken'
    	}
    }
}
  
  

}

return this;
