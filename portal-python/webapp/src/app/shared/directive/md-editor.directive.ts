import {AfterViewInit, Attribute, Directive, EventEmitter, Output} from '@angular/core';
import {MdEditorConfig} from './md-editor-config';

declare var editormd: any;
declare var $: any;

@Directive({
    selector: '[mdEditor]'
})
export class MdEditorDirective implements AfterViewInit {
    @Output() onEditorChange: EventEmitter<string> = new EventEmitter<string>(); // 发射器
    editor: any; // editormd编辑器

    constructor(@Attribute('id') private id: string) {
    }

    ngAfterViewInit(): void {
        this.editor = editormd(this.id, new MdEditorConfig()); // 创建编辑器

        const out = this.onEditorChange;
        const textarea = $('#' + this.id + ' :first'); // 获取textarea元素

        // 当编辑器内容改变时，触发textarea的change事件
        this.editor.on('change', function () {
            out.emit(textarea.val());
        });
    }

}
