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

	      					if (params.tool == 'gradle') {
								//caso Gradle 
								if (params.stage.contains('Build-Tst') || params.stage.contains('Sonar') || params.stage.contains('Run') || params.stage.contains('Test') || params.stage.contains('Nexus')) {
									println "INFO: Etapas validas -> OK!"		
								}
								else {
									error 'Parametros invalidos: Gradle, ejecución interrumpida: Steps no reconocidos'
								}
      						}
							else if (params.tool == 'maven') {
								// caso Maven -> Compile;Test-Code;Build;Sonar;Run;Test-App;Nexus
								if (params.stage.contains('Compile') || params.stage.contains('Test-Code') || params.stage.contains('Build') || params.stage.contains('Sonar') || params.stage.contains('Run') || params.stage.contains('Test-App') || params.stage.contains('Nexus')) {
									println "INFO: Etapas validas -> OK!"		
								}
								else {
									error 'Parametros invalidos: Maven, ejecución interrumpida: Steps no reconocidos'
								}      					
	      					}
	      					else {
	      						error "ERROR: parametro <Herramienta> incorrecto"
	      					}
						}
                }
            }
    	}
        stage('Pipeline') {
            steps {
                script {
			bat 'set'
                	env.ETAPA = ''
                	env.PARAM_STAGE = params.stage
                	
                	if (params.stage.isEmpty()) {
						// ejecutar todos los steps
						println "INFO: Ejecución de TODOS los stages"

						params.tool

						if (params.tool == 'gradle') { 
								//def ejecucion = load 'gradle.groovy'
								//ejecucion.call()
								gradle.call()
						}
						else if (params.tool == 'maven') {
								maven.call()
						}
						else {
							error "ERROR: parametro <Herramienta> incorrecto"
						}
					}
					else {
						// ejecutar el o los stages ingresados
						println "INFO: Ejecución de uno o mas stages"
						if (params.tool == 'gradle') { 
								gradle.call()
						}
						else if (params.tool == 'maven') {
								maven.call()
						}
						else {
							error "ERROR: parametro <Herramienta> incorrecto"
						}
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
