"""Restructure ccal HTML for calendar.css without recalculating any dates.

Usage: python restyle.py ../1.htm calendar-matched.htm
Keep calendar.css beside this script. The generated HTML embeds its contents.
"""
from html import escape
from html.parser import HTMLParser
from pathlib import Path
import re
import sys


class CalendarParser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.rows = []
        self.row = None
        self.cell = None

    def handle_starttag(self, tag, attrs):
        if tag == 'tr':
            self.row = []
        elif tag in ('td', 'th'):
            self.cell = ''

    def handle_data(self, data):
        if self.cell is not None:
            self.cell += data

    def handle_endtag(self, tag):
        if tag in ('td', 'th') and self.cell is not None:
            self.row.append(self.cell)
            self.cell = None
        elif tag == 'tr' and self.row is not None:
            self.rows.append(self.row)
            self.row = None


def convert(source, destination):
    parser = CalendarParser()
    parser.feed(Path(source).read_text(encoding='utf-8'))
    months = []
    for row in parser.rows:
        if len(row) == 1:
            months.append({'title': row[0], 'rows': []})
        elif months:
            months[-1]['rows'].append(row)
    if len(months) != 12:
        raise ValueError('Expected twelve month headings in the supplied ccal HTML.')
    years = {re.search(r'\b\d{4}\b', m['title']).group() for m in months}
    if len(years) != 1:
        raise ValueError('Month headings disagree about the year.')
    year = years.pop()
    css = Path(__file__).with_name('calendar.css').read_text(encoding='utf-8')
    result = ['<!doctype html>', '<html lang="zh-Hans">', '<head>',
              '<meta charset="utf-8">', '<meta name="viewport" content="width=device-width, initial-scale=1">',
              f'<title>Chinese Calendar {year}</title>', '<style>', css, '</style>',
              '</head>', '<body>', '<main class="sheet">',
              f'<h1 class="year">{year}</h1>', '<div class="calendar">']
    for number, month in enumerate(months, 1):
        title = escape(month['title'])
        result += [f'<section class="month" data-month="{number}" aria-labelledby="month-{number}">',
                   f'<h2 class="sr-only" id="month-{number}">{title}</h2>',
                   '<table>', f'<caption class="sr-only">{title}</caption>', '<thead><tr>']
        weekdays, *weeks = month['rows']
        for weekday in weekdays:
            # Visible labels follow the reference; full bilingual text stays accessible.
            result.append(f'<th scope="col" aria-label="{escape(weekday)}">{escape(weekday.split()[-1])}</th>')
        result.append('</tr></thead><tbody>')
        for week in weeks:
            result.append('<tr>')
            for cell in week:
                text = cell.strip()
                if not text:
                    result.append('<td></td>')
                    continue
                match = re.fullmatch(r'(\d{1,2})\s+(.+)', text)
                if not match:
                    raise ValueError(f'Unexpected calendar cell: {text!r}')
                day, lunar = match.groups()
                result.append(f'<td><span class="day">{day}</span><span class="lunar">{escape(lunar)}</span></td>')
            result.append('</tr>')
        result += ['</tbody></table>', '</section>']
    result += ['</div>', '</main>', '</body>', '</html>']
    Path(destination).write_text('\n'.join(result) + '\n', encoding='utf-8')


if __name__ == '__main__':
    if len(sys.argv) != 3:
        raise SystemExit('Usage: python restyle.py SOURCE.htm OUTPUT.htm')
    convert(sys.argv[1], sys.argv[2])
