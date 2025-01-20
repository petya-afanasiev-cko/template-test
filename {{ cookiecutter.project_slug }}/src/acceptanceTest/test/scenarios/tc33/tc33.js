const
    ScenarioBuilder = require("../../scenario-builder"),
    s3Helper = require("../../helpers/s3-helper"),
    kafkaHelper = require("../../helpers/kafka-helper");

feature("MC TC33 file @presentment-settled", function () {
  let context;

  beforeEachScenario(async () => {
    context = {};
    await kafkaHelper.startConsumer();
  });

  new ScenarioBuilder()
  .addScenario("Valid 'TC33' Capture emitted @capture", s => s
      .given("a valid Confirmation file", () => {
        context.incomingFile = config.tc33File;
      })
      .when("file is uploaded to S3 and processed", async () => {
        await s3Helper.uploadFile(context.incomingFile);
        context.events = await kafkaHelper.getEvents();
      })
      .then("'PresentmentSettled' Capture is valid", async () => {
        console.log("TODO")
      })
  ).build();

});