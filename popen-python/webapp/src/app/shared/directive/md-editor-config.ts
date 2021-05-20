export class MdEditorConfig {
    public width = '100%';
    public height = '500';
    public path = 'assets/js/editor.md/lib/';
    public codeFold: true;
    public searchReplace = true;
    public toolbar = true;
    public emoji = true;
    public taskList = true;
    public tex = true;
    public readOnly = false;
    public tocm = true;
    public watch = true;
    public placeholder = '请输入Markdown格式的文档......';
    public previewCodeHighlight = true;
    public saveHTMLToTextarea = true;
    public markdown = '';
    public flowChart = true;
    public syncScrolling = true;
    public sequenceDiagram = true;
    public imageUpload = true;
    public imageFormats = ['jpg', 'jpeg', 'gif', 'png', 'bmp', 'webp'];
    public imageUploadURL = window.location.protocol + "//" + window.location.host + "/uaa/special/editormd";
}
