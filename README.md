# DevDynamicsMetricsAPI

📘 Project Overview
The Metrics Dashboard is a Spring Boot application designed to monitor DevOps performance by calculating Change Failure Rate and Mean Time to Recovery (MTTR). It integrates with PagerDuty to fetch incident data and Jenkins to collect deployment data. The metrics are exposed via RESTful APIs and can be filtered using custom date ranges.

This project helps engineering and DevOps teams measure system reliability, identify weak areas, and improve release quality over time.

📘 Metrics Dashboard – Documentation
🔧 1. Setup & Installation
Prerequisites:

Java 17+

Maven

MySQL

Valid PagerDuty & Jenkins API tokens(Currently kept Local)

**Steps to run locally:**
# 1. Clone the repo
git clone https://github.com/<your-org>/metrics-dashboard.git
cd metrics-dashboard

# 2. Add your credentials in application.properties
cp src/main/resources/application.example.properties src/main/resources/application.properties

# 3. Build & Run
# Build the project
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run

🔐 2. Configuration
Required Configs (in application.properties):

# PagerDuty
pagerduty.api.token=YOUR_TOKEN
pagerduty.api.url=https://api.pagerduty.com

# Jenkins
jenkins.api.token=YOUR_JENKINS_TOKEN
jenkins.api.url=https://jenkins.example.com

# Date Range
metrics.default.start=2024-01-01
metrics.default.end=2024-01-31

CURLS
Get Change Failure Rate and MTTR
curl --request GET \
  --url "http://localhost:8081/api/metrics/calculate?from=2024-06-01&to=2024-06-20" \
  --header "Accept: application/json"

Get Incidents from the Database (Not direct from PagerDuty)
curl --request GET \
  --url "http://localhost:8081/api/incidents?from=2024-06-01&to=2024-06-20" \
  --header "Accept: application/json"

Get Deployments from the Database (Jenkins-based)
curl --request GET \
  --url "http://localhost:8081/api/deployments?from=2024-06-01&to=2024-06-20" \
  --header "Accept: application/json"

Fetch Incidents from PagerDuty API (used in code)
curl --request GET \
  --url "https://api.pagerduty.com/incidents" \
  --header "Accept: application/json" \
  --header "Authorization: Token token=<YOUR_PAGERDUTY_API_TOKEN>" \
  --header "Content-Type: application/json"

Fetch Jenkins Deployments API (if used directly via shell)
curl --request GET \
  --url "<YOUR_JENKINS_URL>/job/<job_name>/api/json" \
  --user "<jenkins_user>:<jenkins_token>"

💡 Key Components
Spring Boot API: Hosts all metric calculation logic and REST endpoints.

PagerDuty Integration: Pulls incident data via secure token-authenticated HTTP calls.

Jenkins Integration: Fetches successful deployments within a date range.

MySQL Database: Persists incidents and deployments for performance and querying.

Metrics Logic: Computes failure rate and MTTR using stored data.

Swagger UI (optional): Can be integrated for easy API exploration.



