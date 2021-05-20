import {AfterViewInit, Attribute, Directive} from '@angular/core';

declare var editormd: any;

@Directive({
    selector: '[mdView]'
})
export class MdViewDirective implements AfterViewInit {

    constructor(@Attribute('id') private id: string) {
    }

    ngAfterViewInit(): void {
        editormd.markdownToHTML(this.id, {
            htmlDecode      : "style,script,iframe",  // you can filter tags decode
            emoji           : true,
            taskList        : true,
            tex             : true,  // 默认不解析
        });
    }

}
