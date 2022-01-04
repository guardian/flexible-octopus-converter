# flexible-octopus-converter

This repo contains the lambda function which converts incoming Octopus JSON into Thrift. The lambda gets records from one Kinesis stream, validates and parses them, them puts them on a second Kinesis stream, which is made available to Editorial Tools services.

## Deployment

The lambda is deployed via RiffRaff, using the [Editorial Tools::Octopus Conversion Lambda](https://riffraff.gutools.co.uk/deployment/history?projectName=Editorial%20Tools%3A%3AOctopus%20Conversion%20Lambda&page=1) project. On merge to main, the lambda will be automatically deployed.

## Logs

Logs are sent to Cloudwatch and the lambda's performance can be monitored in [Grafana](https://metrics.gutools.co.uk/d/gLzfI4ZGz/octopus-overview).

## Links

The cloud formation for these resources can be found in the [editorial-tools-platform](https://github.com/guardian/editorial-tools-platform/) repo.

The thrift model can be found in the [flexible-octopus-model](https://github.com/guardian/flexible-octopus-model) repo.

## Testing

The project includes some basic tests for the functionality of the lambda. It is also possible to test a production environment by putting data onto the kinesis stream in base64. You can use the test resources as a starting point:

Base64 encoding a file:
`EXAMPLE=$(cat ./src/test/resources/example.json | base64)`

Putting a record onto the stream:
`aws kinesis put-record --stream-name <STREAM_NAME> --data $EXAMPLE --profile <PROFILE> --partition-key example --region <REGION>`