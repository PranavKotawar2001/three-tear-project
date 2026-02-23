pipeline{
    agent any
    stages{
        stage("Pull"){
            steps{
                git 'https://github.com/PranavKotawar2001/three-tear-project.git'
            }
        }
        stage("Build"){
            steps{
                sh '''cd frontend
                    npm install
                    npm run build'''
            }
        }
        stage("Move-To-S3"){
            steps{
                sh '''cd frontend
                   aws s3 sync dist/ s3://pranav-backend-bucket-00000000/'''
            }
        }
    }
}