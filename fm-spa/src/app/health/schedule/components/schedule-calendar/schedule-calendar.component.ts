import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {ScheduleItem, ScheduleList} from '../../../shared/services/schedule.service';

@Component({
  selector: 'app-schedule-calendar',
  styleUrls: ['schedule-calendar.component.scss'],
  templateUrl: 'schedule-calendar.component.html'
})
export class ScheduleCalendarComponent implements OnChanges {
  selectedDayIndex: number;
  selectedDay: Date;
  selectedWeek: Date;
  sections = [
    { key: 'morning', name: 'Morning' },
    { key: 'lunch', name: 'Lunch' },
    { key: 'evening', name: 'Evening' },
    { key: 'snacks', name: 'Snacks and Drinks' }
  ];
  @Input() set date(date: Date) {
    this.selectedDay = new Date(date.getTime());
  }
  @Input() items: ScheduleList;
  @Output() changeDate = new EventEmitter<Date>();
  @Output() selectEvent = new EventEmitter<any>();
  constructor() {}

  ngOnChanges(changes: SimpleChanges): void {
    this.selectedDayIndex = this.getToday(this.selectedDay);
    this.selectedWeek = this.getStartOfWeek(new Date(this.selectedDay));
  }

  getSection(name: string): ScheduleItem {
    // console.log(this.items && this.items[name] || {});
    return this.items && this.items[name] || {};
  }

  selectSection({type, assigned, data}: any, section: string) {
    const day = this.selectedDay;
    this.selectEvent.emit({
      type,
      assigned,
      section,
      day,
      data
    });
  }

  selectDay(index: number) {
    const selectedDay = new Date(this.selectedWeek);
    selectedDay.setDate(selectedDay.getDate() + index);
    this.changeDate.emit(selectedDay);
  }

  onChange(weekOffset: number) {
    const startOfWeek = this.getStartOfWeek(new Date());
    const startDate = (
      new Date(startOfWeek.getFullYear(), startOfWeek.getMonth(), startOfWeek.getDate())
    );
    startDate.setDate(startDate.getDate() + (weekOffset * 7));
    this.changeDate.emit(startDate);
  }

  private getToday(date: Date) {
    let today = date.getDay() - 1;
    if (today < 0) {
      today = 6;
    }
    return today;
  }

  private getStartOfWeek(date: Date) {
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(date.setDate(diff));
  }
}
