import { Pipe, PipeTransform, Injectable} from '@angular/core';

@Pipe({
    name: 'arrayFilter'
})

@Injectable()
export class ArrayFilter implements PipeTransform {
    transform(items: any[], filter: string): any[] {
        if (!items) {
            return [];
        }

        if (!filter) {
            return items;
        }

        return items.filter((item) =>
            this.isMatch(item, filter));
    }

    isMatch(item: any, filter: string): boolean {
        const props = Object.keys(item);

        for (const prop of props) {
            if (typeof(item[prop])  === 'string') {
                if (item[prop].includes(filter)) {
                    return true;
                }
            }
        }

        return false;
    }
}
