pipeline {
    agent any
    stages {
        stage ('Build Servlet Project') {
            steps {
                /*For windows machine */
              // bat  './gradlew clean build --no-daemon'

                /*For Mac & Linux machine */
                sh  './gradlew clean build --no-daemon'
            }
 
            post{
                success{
                    echo 'Now Archiving ....'
 
                    archiveArtifacts artifacts : 'build/libs/*.jar'
                }
            }
        }
    }
}