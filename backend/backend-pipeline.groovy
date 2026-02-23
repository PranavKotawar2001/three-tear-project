pipeline{
    agent any
    stages{
        stage('Pull'){
            steps{
                git 'https://github.com/PranavKotawar2001/three-tear-project.git'
            }
        }
         stage('Build'){
            steps{
                sh '''cd backend
                   mvn clean package -DskipTests'''   
            }
        }
         stage('Test'){
            steps{
                withSonarQubeEnv(installationName: 'Sonar', credentialsId: 'Sonar-secret') {
                sh '''cd backend
                    mvn sonar:sonar -Dsonar.projectKey=backend'''
                }   
            }
        }
        stage('Docker-Image-Build'){
            steps{
                sh '''cd backend
                   docker build -t pranavsudhirkotawar/backend:latest .
                   docker push pranavsudhirkotawar/backend:latest
                   docker rmi pranavsudhirkotawar/backend:latest''' 
            }
        }
        stage('Build'){
            steps{
                sh '''cd backend
                kubectl apply -f k8s/'''  
            }
        }
    }
}