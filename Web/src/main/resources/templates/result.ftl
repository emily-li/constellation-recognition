<#include "header.ftl">
<div class="pageheader">
    <h1 align="center">${constellationresult}</h1>
    <hr>
</div>
<br/>
<div class="container">
    <div class="row">
        <div class="col-md-6">
            <img class="img-responsive center-block" src="/constellations/${constellationfile}.png"/>
        </div>
        <div class="col-md-6">
            <img class="img-responsive center-block" src="/upload/file/${fileid}"/>
        </div>
    </div>
</div>
<#include "footer.ftl">