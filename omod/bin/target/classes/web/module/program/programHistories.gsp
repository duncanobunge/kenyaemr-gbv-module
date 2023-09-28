<% programs.each { descriptor -> %>
${ ui.includeFragment("gbv", "program/programHistory", [ patient: patient, program: descriptor.target, showClinicalData: showClinicalData ]) }
<% } %>