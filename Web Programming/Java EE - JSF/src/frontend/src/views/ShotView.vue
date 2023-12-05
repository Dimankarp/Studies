
<script setup>

import {ref} from "vue";

const isHit = ref(true)

</script>

<template>
  <div class="shot-screen" id="shot-outcome-div" tabindex="0">
    <h1 v-if="isHit">That's a hit!</h1>
    <h1 v-else>Ooops...You've missed!</h1>
  </div>
  <div class="form-container">
    <form class="coords-form" id="main-coords-form">
      <h1 id="test">Try not to miss!</h1>
      <div class="coords-input-pair">
        <h:outputLabel for="xCoord-textbox" value="X Coordinate:"/>
        <h:inputText id="xCoord-textbox" value="#{shot.x}" required="true" maxlength="14"
                     validatorMessage="Must be a double between -5 and 5!"
                     converterMessage="Must be a number!">
          <f:validateDoubleRange minimum="-5" maximum="5"/>
          <f:ajax event="blur" render="xCoord-message"/>
        </h:inputText>
        <p:slider id="x-slider" widgetVar="x-slide"
                  for="xCoord-textbox" minValue="-5" maxValue="5" step="0.1" styleClass="form-slider">
          <p:ajax event="slideEnd" render="xCoord-message"/>
        </p:slider>
        <h:message id="xCoord-message" for="xCoord-textbox"/>
      </div>

      <div class="coords-input-pair">
        <h:outputLabel for="yCoord-textbox" value="Y Coordinate:"/>
        <h:inputText id="yCoord-textbox" value="#{shot.y}" required="true" maxlength="14"
                     validatorMessage="Must be a double between -3 and 3!"
                     converterMessage="Must be a number!">
          <f:validateDoubleRange minimum="-3" maximum="3"/>
          <f:ajax event="blur" render="yCoord-message"/>
        </h:inputText>
        <h:message id="yCoord-message" for="yCoord-textbox"/>
      </div>


      <div class="coords-input-pair">
        <h:outputLabel for="radius-textbox" value="Radius:"/>
        <h:inputText id="radius-textbox" value="#{shot.radius}" required="true" maxlength="14"
                     validatorMessage="Must be a double between 1 and 4!"
                     converterMessage="Must be a number!">
          <f:validateDoubleRange minimum="1" maximum="4"/>
          <f:ajax event="blur" render="radius-message"/>
        </h:inputText>
        <h:message id="radius-message" for="radius-textbox"/>
        <h:inputHidden id="time-zone-input" value="#{dateFormatter.zone}">
          <f:converter converterId="mitya.sites.face.converters.TimeZoneConverter"/>
        </h:inputHidden>
        <!-- <script>document.getElementById("main-coords-form:time-zone-input").value = Intl.DateTimeFormat().resolvedOptions().timeZone </script> -->
      </div>
      <h:commandButton id="coords-submit-btn" action="#{shotProcesser.process}" value="Shoot!">
        <f:ajax execute="@form" render="record-data-table
                        hit-text miss-text record-table shot-outcome-group
                        radius-message yCoord-textbox xCoord-textbox  shot-json"
                onevent="function (data){ajaxExecuted(data)}"/>
      </h:commandButton>
    </form>
    <h:outputText style="display: none" id="shot-json" value="#{shotRecorder.getJSON()}"/>

  </div>

  <div class="canvas-container">
    <canvas width="400px" height="400px" id="target-canvas"></canvas>
    <div class="target-settings">
      <h:selectBooleanCheckbox type="checkbox" ps:id="child-mode-check"/>
      <h:selectBooleanCheckbox type="checkbox" ps:id="prefire-mode-check"/>
    </div>


  </div>

  <div id="record-table" class="record-table-container">
    <h:dataTable id="record-data-table" value="#{shotRecorder.recordedShots.stream().sorted().toList()}" var="record">
      <h:column>
        <f:facet name="header">Is Hit</f:facet>
        #{record.hit ? "Yes!": "No"}
      </h:column>

      <h:column>
        <f:facet name="header">X</f:facet>
        #{record.x}
      </h:column>

      <h:column>
        <f:facet name="header">Y</f:facet>
        #{record.y}
      </h:column>

      <h:column>
        <f:facet name="header">Radius</f:facet>
        #{record.radius}
      </h:column>

      <h:column>
        <f:facet name="header">Time</f:facet>
        #{dateFormatter.format(record.timeStamp)}
      </h:column>
    </h:dataTable>
  </div>

</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}
</style>
