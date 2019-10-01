import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-schedule-days',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['schedule-days.component.scss'],
  templateUrl: 'schedule-days.component.html'
})
export class ScheduleDaysComponent {
  days = ['M', 'T', 'W', 'T', 'F', 'S', 'S'];
  @Input() selected: number;
  @Output() selectEvent = new EventEmitter<number>();

  selectDay(index: number) {
    this.selectEvent.emit(index);
  }
}
