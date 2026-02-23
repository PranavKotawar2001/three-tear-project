# Step-by-Step Three Tier Application Deployment

## Deployment Strategy

- Database : RDS (MySQL)
- Backend : EKS (Kubernetes)
- Frontend : S3 (Static Website)

---

## Pre Requisites

- Terraform scripts for:
  - RDS
  - EKS
  - S3
- Three Ubuntu EC2 instances:
  - Jenkins
  - SonarQube
  - Terraform
- Terraform EC2 instance with IAM Role attached

---

## Backend And Database Part

## Launch Terraform EC2 Instance with IAM Role Attach

- Take ssh

### Step 1: Update Instance and Install Terraform

#### Update Instance

```bash
sudo apt update -y
```

#### Install Terraform

```bash
wget -O - https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
sudo apt update && sudo apt install terraform -y
```

#### Verify Terraform Installation

```bash
terraform version
```

### Step 2: Add Terraform Code

- Pull Terraform code from GitHub or
- Create Terraform files manually inside the instance

```bash
git clone <terraform-repo-url>
cd <terraform-folder>
```

### Step 3: Run Terraform Commands

#### Initialize Terraform

```bash
terraform init
```

#### Plan Terraform Resources

```bash
terraform plan
```

#### Create Terraform Resources

```bash
terraform apply
```

#### If You created tfvars file

```bash
terraform apply -var-file=< Path of tfvars file >
```

### Step 4: Destroy Resources (After Project Completion)

```bash
terraform destroy
```

---

## Lonch Sonarqube Instance

- Enable port No- 9000
- take ssh

### Step 1:- Update the instance

```bash
apt update
```

### Srtep 2:- Install Sonarqube

#### Install and configure Database

```bash
apt install openjdk-17-jdk -y
```

```bash
apt install postgresql -y
```

```bash
systemctl start postgresql
```

```bash
sudo -u postgres psql
```

- Run inside postgres psql

```bash
>> CREATE USER linux PASSWORD 'redhat';
>> CREATE DATABASE sonarqube;
>> GRANT ALL PRIVILEGES ON DATABASE sonarqube TO linux;
>> \c sonarqube;
>> GRANT ALL PRIVILEGES ON SCHEMA public TO linux;
>> \q
```

#### Configure Linux Machine

```bash
sysctl -w vm.max_map_count=524288
```

```bash
sysctl -w fs.file-max=131072
```

```bash
ulimit -n 131072
```

```bash
ulimit -u 8192
```

#### Install and Configure Sonarqube

```bash
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-25.5.0.107428.zip
```

```bash
apt install unzip -y
```

```bash
unzip sonarqube-25.5.0.107428.zip
```

```bash
mv sonarqube-25.5.0.107428 /opt/sonar
```

```bash
cd /opt/sonar
```

- Do changes inside sonar.properties

```bash
   vim conf/sonar.properties
```

- Add inside sonar.properties at proper location

```bash
>> sonar.jdbc.username=linux
>> sonar.jdbc.password=redhat
>> sonar.jdbc.url=jdbc:postgresql://localhost/sonarqube
```

```bash
useradd sonar -m
```

```bash
chown sonar:sonar -R /opt/sonar
```

```bash
su sonar
```

```bash
cd /opt/sonar/bin/linux-x86-64
```

```bash
./sonar.sh start
```

```bash
./sonar.sh status
```

### Srtep 3:- Take Access Of Sonarqube

```bash
http://< public IP of sonarqube instance >:9000
```

### Srtep 4:- Login to Sonarqube

- Initial Username and Password is
  - Username:- admin
  - password:- admin
- Change The Password accourding to requirment

### Srtep 5:- Folow the steps

#### 5.1 Go to **_ Create a local project _**

#### 5.2 Give the **_ project name _** and click on Next

#### 5.3 Click on **_ Use the global setting _** and click on create Project

#### 5.4 click on **_ Locally _**

#### 5.5 gunerae Token name (save the token in local device ) and click on continue

#### 5.6 Click on **_ MAVEN _** (Save Gunerated MAVEN script in local device)

---

## Lonch Jenkins Instance

- Enable Port 8080
- Make sure to fulfill Jenkins requirement (Use instance c7i-flex.large)
- take ssh

### Step 1:- Update the instance

```bash
apt update
```

### step 2:- Install Jenkins

```bash
apt install openjdk-17-jdk -y
```

```bash
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt update
sudo apt install jenkins
```

```bash
systemctl start jenkins
```

```bash
systemctl status jebkins
```

### Step 3:- Take access of jenkins

```bash
http://< Jenkins instance public IP >:8080
```

### Step 4:- get jenkins initial password inside jenkins instance

```bash
cat /var/lib/jenkins/secrets/initialAdminPassword
```

### Step 5:- Folow the steps

#### 5.1:- Click on **_ select plugins to install _** and clock on next

#### 5.2:- Fill the creadential like username password and click on **_ save nad Continue _** then click on ok and then click on **_ start using jenkins_**

#### 5.3:- Go to settings > Plugins > install all required plugins

- pipeline
- pipeline stage view
- git
- github
- sonarqube

### Step 6:- Configure sonarqube

#### 6.1:- Go to settings

#### 6.2:- Click on **_ System _**

#### 6.3:- Scroll down to **_ SonarQube servers _**

#### 6.4:- Click on **_ Add SonarQube _**

#### 6.5:- Fill the Credentials

#### 6.6:- Click on **_ Add > Secret Text _**

#### 6.7:- Give the name and pest token which was generated while creating sonarqube and click on create

#### 6.8:- Select the secret text and click on save and apply

### Step 7:- Go to Jenkins Instance And Install

- Docker
- AWS-CLI
- kubectl
- maven
- Login to Docker-Hub

#### 7.1:- Install Docker

```bash
apt install docker.io -y
```

```bash
docker -v
```

**_ Add jenkins to Docker group _**

```bash
usermod -aG docker jenkins
```

#### 7.2:- Install AWS-CLI

- Gunerate **_ Access Key _** and **_ Secret Key _** Of AWS account Or IAM role

```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
```

```bash
apt install unzip -y
```

```bash
unzip awscliv2.zip
```

```bash
sudo ./aws/install
```

```bash
aws configure
```

- AWS Access Key ID = < Pest Access Key ID >
- AWS Secret Access Key = < Pest Secret Access Key >
- Default region name = < Give region name >
- Default output format = json

**_ Check Whether .aws File is created or not _**

```bash
la -a
```

**_ Give AWS-CLI Permission to jenkins user _**

```bash
cp -rv .aws/ /var/lib/jenkins/
```

```bash
 chown -R jenkins /var/lib/jenkins/.aws/
```

#### 7.3:- Install kubectl

- Enable HTTP and HTTPS in security-group

```bash
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
```

```bash
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

```bash
kubectl version --client
```

**_ Take a Access Of EKS cluster _**

```bash
aws eks update-kubecongif --name < cluster name > --region < region >
```

- check whether nodes are visible or not

```bash
kubectl get nodes
```

- Check whether .kube file is created or not

```bash
ls -a
```

```bash
cp -rv .kube/ /var/lib/jenkins/
```

```bash
 chown -R jenkins /var/lib/jenkins/.kube/
```

#### 7.4:- Install maven

```bash
apt install maven
```

#### 7.5:- Login Docker-Hub

```bash
docker login
```

**_ give all Credentials and login to docker hub and login _**

### All the prerequisite of Jenkins is completed

## Start implementing project

### Step 8:- Take access of RDS database

#### 8.1:- Go to Jenkins instance and take access of RDS database

```bash
apt install mysql-client
```

```bash
mysql -h <End Point Of RDS> -P 3306 -u < Username > -p
```

- press Enter
- Give password

#### 8.2:- Create Database

```bash
create database flightdb;
```

```bash
show databases;
```

#### 8.3:- Do changes in backend ** application.properties **

- spring.datasource.url=jdbc:< Give RDS endpoint >:3306/flightdb?
- spring.datasource.username=< Give Username >
- spring.datasource.password=< Give Password >
- Push Code to Github

### Step 9:- Start writing Jenkins pipeline

- click on New Item >
- Give Pipeline Name >
- Select Pipeline and Click on OK

#### 9.1:- Pull Stage

```bash
pipeline{
    agent any
    stages{
        stage('Pull'){
            steps{
                git 'https://github.com/PranavKotawar2001/three-tear-project.git'
            }
        }
    }
}
```

#### 9.2:- Build Stage

```bash
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

    }
}
```

#### 9.3:- Test Stage

```bash
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
    }
}
```

#### 9.4:- Image Build Stage

```bash
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
                   docker build -t < Image name as per docker hub> .
                   docker push < Image name as per docker hub>
                   docker rmi < Image name as per docker hub>'''
            }
        }
    }
}
```

- Update the Deployment.yaml file
- Add image name inside Deployment.yaml
- push to github

#### 9.5:- Deploy Stage

```bash
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
                   docker build -t < Image name as per docker hub> .
                   docker push < Image name as per docker hub>
                   docker rmi < Image name as per docker hub>'''
            }
        }
        stage('Deploy'){
            steps{
                sh '''cd backend
                kubectl apply -f k8s/'''
            }
        }
    }
}
```

#### 9.6:- Take backend End Point

```bash
kubectl get svc
```

- Insert the service endpoint in frontend > **_ .env _** file

## End of Backend And Database

---

## Frontend Part

### Step 1:- Install Nodejs

```bash
apt install nodejs npm -y
```

### Step 2:- Start Creating Pipeline

- S3 Bucket Should Have Public Access

#### 2.1:- Pull Stage

```bash
pipeline{
    agent any
    stages{
        stage("Pull"){
            steps{
                git 'https://github.com/PranavKotawar2001/three-tear-project.git'
            }
        }
    }
}
```

#### 2.2:- Build Stage

```bash
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
    }
}
```

#### 2.3:- Move To S3 Stage

```bash
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
```

#### 2.4:- Take Access

**_ Go to S3 bucket and take a hit the URL _**

## End Of Project

---
