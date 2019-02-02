#!/usr/bin/env groovy

pipeline {
    agent any
    
    environment {
        oracleUsername = credentials('oracleUsername')
        oraclePassword = credentials('oraclePassword')
    }    

    stages {
        stage('Prepare') {
            steps {
                echo "Checkout sources"
                git "https://github.com/edigonzales/gretl-ng.git/"
            }
        }
        
        stage('Build') {
            steps {
                sh './gradlew --no-daemon clean gretl:classes'
            }
        }

        stage('Unit Tests') {
            steps {
                sh './gradlew --no-daemon gretl:test gretl:dbTest'
                publishHTML target: [
                    reportName : 'Gradle Tests',
                    reportDir:   'gretl/build/reports/tests/test', 
                    reportFiles: 'index.html',
                    keepAll: true,
                    alwaysLinkToLastBuild: true,
                    allowMissing: false
                ]                
            }
        }              
    }
    post {
        always {
            deleteDir() 
        }
    }
}