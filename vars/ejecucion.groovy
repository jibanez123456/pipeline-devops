def call(){

	pipeline {
    agent any

	parameters { 
		choice(name: 'tool', choices: ['gradle', 'maven'], description: 'Selección herramienta de Construcción:')
		string(name: 'stage', defaultValue: '', description: 'Ingrese parametro->stage a ejecutar:')
	}

	
    
    stages {
    	stage('Validacion') { // validación del parametro stage: vacio, un stage o todos los stages
            steps {
                script {
                		env.ETAPA = 'Validacion'
						println "INFO: Validacion parametros"

						if (params.stage.isEmpty()) {
								println "INFO: stage=vacio -> ejecutar TODOS"		
						} 
						else {
							String[] params_stage_list = params.stage.split(';')
							for( String values : params_stage_list )
	      						println(values);

	      					if (params_stage_list.size() >= 1) {
								println "INFO: Cantidad de Etapas -> OK!"		
	      					}
	      					else {
								error 'Parametros invalidos, ejecución interrumpida: Cantidad de Steps'

	      					}

							if (env.GIT_BRANCH.contains('feature') || env.GIT_BRANCH.contains('develop')) {
								// pipeline-ci: ■ 'buildAndTest','sonar','runJar','rest','nexusCI'
								if (params.stage.contains('buildAndTest') || params.stage.contains('sonar') || params.stage.contains('runJar') || params.stage.contains('rest') || params.stage.contains('nexusCI')) {
									println "INFO: pipeline-ci: Etapas validas -> OK!"		
								}
								else {
									error 'Parametros invalidos: CI -> ejecución interrumpida: Steps no reconocidos'
								}
      						}
							else if (env.GIT_BRANCH.contains('release')) {
								// caso pilepile-cd -> ■ 'downloadNexus','runDownloadedJar','rest','nexusCD'
								if (params.stage.contains('downloadNexus') || params.stage.contains('runDownloadedJar') || params.stage.contains('rest') || params.stage.contains('nexusCD')) {
									println "INFO: pipeline-cd: Etapas validas -> OK!"		
								}
								else {
									error 'Parametros invalidos: CD -> ejecución interrumpida: Steps no reconocidos'
								}      					
	      					}
	      					else {
	      						error "ERROR: parametro <Herramienta> incorrecto"
	      					}
	      					println "INFO: Ejecución de 1 o + stages"
						}
                }
            }
    	}
        stage('Pipeline') {
            steps {
                script {

					bat 'set'
					figlet params.tool
			
                	env.ETAPA = ''
                	env.PARAM_STAGE = params.stage
                	def pipeline_type = ''
					params.tool

					if (params.tool == 'gradle') { 
						if (env.GIT_BRANCH.contains('feature') || env.GIT_BRANCH.contains('develop')) {
							pipeline_type = 'Integracion Continua'
							figlet pipeline_type
							pipeline_ci.call()
						}
						else if (env.GIT_BRANCH.contains('release')) {
							pipeline_type = 'Entrega Continua'
							figlet pipeline_type
							pipeline_cd.call()
						}
					}
					else if (params.tool == 'maven') {
						println "INFO: no implementado"
					}
					else {
						error "ERROR: parametro <Herramienta> incorrecto"
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
