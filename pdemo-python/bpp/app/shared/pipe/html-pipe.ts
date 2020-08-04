import {Injectable, Pipe, PipeTransform} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

@Pipe({
    name: 'html'
})
@Injectable()
export class HtmlPipe implements PipeTransform {
    constructor(private sanitizer: DomSanitizer) {
    }

    transform(style) {

        return style ? this.sanitizer.bypassSecurityTrustHtml(style) : null;
    }

}
