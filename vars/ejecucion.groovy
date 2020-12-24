def call(){

	pipeline {
    agent any

	parameters { 
		choice(name: 'tool', choices: ['gradle', 'maven'], description: 'Selección herramienta de Construcción:')
		string(name: 'stage', defaultValue: '', description: 'Ingrese parametro->stage a ejecutar:')
	}

    
    stages {
        stage('Pipeline') {
            steps {
                script {

                	env.ETAPA = ''

                	if (params.stage.isEmpty()) {
						// ejecutar todos los steps
						println "info: Ejecución de TODOS los stages"

						params.tool

						if (params.tool == 'gradle') { 
								//def ejecucion = load 'gradle.groovy'
								//ejecucion.call()
								gradle.call()
						}
						else {
								//def ejecucion = load 'maven.groovy'
								//ejecucion.call()
								maven.call()
						}
					}
					else {
						// ejecutar el o los stages ingresados
						println "info: Ejecución de uno o mas stages"
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
