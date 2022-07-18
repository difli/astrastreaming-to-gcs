# astrastreaming-to-gcs

This application was build to demo [Change Data Capture (CDC)](https://docs.datastax.com/en/astra-streaming/docs/astream-cdc.html) on [Astra](https://https://astra.datastax.com/) - an open datastack that just works.

astrastreaming-to-gcs subscribes to a change data capture (cdc) data topic on astra streaming and streams the data to a google cloud storage bucket.
The data get"s written in json formated files of an [configurable chunk size](https://github.com/difli/astrastreaming-to-gcs/blob/main/src/main/resources/application.properties#L2).

![alt text](/docs/flow.png)

## Quick start
- [google cloud Console](https://console.cloud.google.com)
    - create a service account
    - create and download a keyfile for the service account. You need to provide your application with the keyfile later to allow the application to write into your bucket.
    - create a google cloud storage bucket in the same google cloud region as your astra streaming tenant. On the bucket provide your service account with permissions. I used these permissions: Storage Admin, Storage Object Admin, Storage Object Creator, Storage Object Viewer
- astra streaming
    -  copy the Broker Service URL (ex. pulsar+ssl://pulsar-gcp-europewest1.streaming.datastax.com:6651), the CDC data topic full name (ex. persistent://cdcdemo/astracdc/data-2ba63c3f-aee4-4a38-b2e5-a49e0600946a-twitter.tweet_extended_by_id) and generate/copy astra streaming token.
- clone this repo
```
git clone https://github.com/difli/astrastreaming-to-gcs.git
```
- cd in folder 'astrastreaming-to-gcs'
```
cd astrastreaming-to-gcs
```
- export credentials you have copied before and other environment variables
```
export GOOGLE_APPLICATION_CREDENTIALS=/Users/dieter.flick/Documents/development/workspaces/workspace-gcp/gcp-lcm-project-fc5981535332.json
export astra_streaming_service_url=pulsar+ssl://pulsar-gcp-europewest1.streaming.datastax.com:6651
export astra_streaming_token=eyJhbGciOiJSUzI...
export astra_streaming_topic_from_cdc_data=persistent://cdcdemo/astracdc/data-2ba63c3f-aee4-4a38-b2e5-a49e0600946a-twitter.tweet_extended_by_id
export spring_profiles_active=astra
```
- run application
```
./mvnw spring-boot:run
```
