pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'jdk'
        git 'git'
    }

    stages {
        // stage ('Build') {
        //   steps {
        //     git branch: "experiment", url: 'https://github.com/koraytugay/groovyship.git'
        //     sh 'mvn clean install'
        //   }
        //   post {
        //     success {
        //       junit 'target/surefire-reports/**/*.xml'
        //     }
        //   }
        // }

        stage('Policy') {
            steps {
                nexusPolicyEvaluation advancedProperties: '', enableDebugLogging: true,
                    failBuildOnNetworkError: false, iqApplication: selectedApplication('jcava-test-app2'),
                    iqScanPatterns: [[scanPattern: 'container:jboss/keycloak:7.0.0']], iqStage: 'build',
                    jobCredentialsId: ''
            }
        }
    }
}