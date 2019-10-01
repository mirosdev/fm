import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MealModel} from '../../../shared/services/meals.service';
import {WorkoutModel} from '../../../shared/services/workouts.service';

@Component({
  selector: 'app-schedule-assign',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['schedule-assign.component.scss'],
  templateUrl: 'schedule-assign.component.html'
})
export class ScheduleAssignComponent implements OnInit {
  private selected = [];
  @Input() section: any;
  @Input() list: MealModel[] | WorkoutModel[];
  @Output() update = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<any>();

  ngOnInit(): void {
    this.selected = [...this.section.assigned];
  }

  toggleItem(inputItem) {
    if (this.exists(inputItem)) {
      this.selected = this.selected.filter(item => item.uuid !== inputItem.uuid);
    } else {
      this.selected = [...this.selected, inputItem];
    }
  }

  getRoute(name: string) {
    return [`../${name}/new`];
  }

  exists(item) {
    return !!this.selected.find(selectedItem => selectedItem.uuid === item.uuid);
  }

  updateAssign() {
    const uuids: string[] = [];
    this.selected.forEach(selected => {
      uuids.push(selected.uuid);
    });
    this.update.emit({
      [this.section.type]: uuids
    });
  }

  cancelAssign() {
    this.cancel.emit();
  }
}
