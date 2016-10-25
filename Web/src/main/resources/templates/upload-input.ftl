<div class="container">
    <br><br>
    <p>

    <div class="input-group">
        <input id="file-input-text" type="text" class="form-control" placeholder="Upload file">
        <label id="file-input-label" class="input-group-btn">
            <form id="upload-form" method="post" enctype="multipart/form-data" action="/upload/file">
                <span class="btn btn-primary">
                    Browse
                    <input id="file-input" type="file" name="file" style="display: none">
                </span>
                <button id="file-upload-button" type="submit" class="btn btn-primary">
                    Upload
                </button>
            </form>
        </label>
    </div>

    <div>
        <p id="processing-status" style="text-align: center; display: none">Processing...</p>
    </div>

    <br>
</div>

<script>
    $('#file-input').on('change', function() {
        var label = $(this).val().replace(/\\/g, '/').replace(/.*\//, '');
        $('#file-input-text').val(label);
    });

    $('#upload-form').submit(function () {
        $('#processing-status').show();
    })
</script>