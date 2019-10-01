import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ScheduleItem} from '../../../shared/services/schedule.service';
import {WorkoutModel} from '../../../shared/services/workouts.service';
import {MealModel} from '../../../shared/services/meals.service';

@Component({
  selector: 'app-schedule-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['schedule-section.component.scss'],
  templateUrl: 'schedule-section.component.html'
})
export class ScheduleSectionComponent {
  @Input() name: string;
  @Input() section: ScheduleItem;
  @Output() selectEvent = new EventEmitter<any>();

  onSelect(type: string, assigned: WorkoutModel[] | MealModel[] = []) {
    let data;
    const mealNames: string[] = [];
    const workoutNames: string[] = [];

    if (this.section.meals) {
      this.section.meals.forEach(meal => {
        mealNames.push(meal.uuid);
      });
    }
    if (this.section.workouts) {
      this.section.workouts.forEach(workout => {
        workoutNames.push(workout.uuid);
      });
    }

    data = {
      meals: mealNames.length > 0 ? mealNames : null,
      workouts: workoutNames.length > 0 ? workoutNames : null,
      section: this.section.section,
      timestamp: this.section.timestamp,
      uuid: this.section.uuid ? this.section.uuid : null
    };

    this.selectEvent.emit({
      type,
      assigned,
      data
    });
  }

  getNames(models: MealModel[] | WorkoutModel[]): string[] {
    const names: string[] = [];
    models.forEach((model: MealModel | WorkoutModel) => {
      names.push(model.name);
    });
    return names;
  }
}
