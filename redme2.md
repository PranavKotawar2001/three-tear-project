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

## Launch Terraform EC2 Instance with IAM Role

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
terraform apply -var-file=<Path of tfvars file >
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
http://<public IP of sonarqube instance>:9000
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
