%====================================================================================
% systemrobotreal description   
%====================================================================================
mqttBroker("192.168.43.61", "1883").
context(ctxrobot, "localhost",  "MQTT", "0" ).
context(ctxdummy, "dummyhost",  "MQTT", "0" ).
 qactor( kb, ctxdummy, "external").
  qactor( resourcemodel, ctxdummy, "external").
  qactor( mindrobot, ctxrobot, "it.unibo.mindrobot.Mindrobot").
  qactor( basicrobot, ctxrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( sonarcollector, ctxrobot, "it.unibo.sonarcollector.Sonarcollector").
  qactor( sonarhandler, ctxrobot, "it.unibo.sonarhandler.Sonarhandler").
  qactor( onerotateforward, ctxrobot, "it.unibo.onerotateforward.Onerotateforward").
  qactor( onecellforward, ctxrobot, "it.unibo.onecellforward.Onecellforward").
  qactor( timer, ctxrobot, "it.unibo.timer.Timer").
  qactor( leds, ctxrobot, "it.unibo.leds.Leds").
  qactor( realforntsonar, ctxrobot, "it.unibo.realforntsonar.Realforntsonar").
  qactor( reallateralsonar, ctxrobot, "it.unibo.reallateralsonar.Reallateralsonar").
  qactor( compass, ctxrobot, "it.unibo.compass.Compass").
