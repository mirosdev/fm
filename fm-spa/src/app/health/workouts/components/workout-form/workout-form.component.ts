import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {WorkoutModel} from '../../../shared/services/workouts.service';
import {FormBuilder, Validators} from '@angular/forms';

@Component({
  selector: 'app-workout-form',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['workout-form.component.scss'],
  templateUrl: 'workout-form.component.html'
})
export class WorkoutFormComponent implements OnChanges {
  toggled = false;
  exists = false;
  @Output() create = new EventEmitter<WorkoutModel>();
  @Output() update = new EventEmitter<WorkoutModel>();
  @Output() remove = new EventEmitter<WorkoutModel>();
  @Input() workout: WorkoutModel;
  @Input() error: string;

  form = this.fb.group({
    name: ['', Validators.required],
    type: 'strength',
    strength: this.fb.group({
      reps: 0,
      sets: 0,
      weight: 0
    }),
    endurance: this.fb.group({
      distance: 0,
      duration: 0
    })
  });

  constructor(private fb: FormBuilder) {}

  get placeholder() {
    return `e.g. ${this.form.get('type').value === 'strength' ? 'Benchpress' : 'Treadmill'}`;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.workout && this.workout.name) {
      this.exists = true;
      const value = this.workout;
      this.form.patchValue(value);
    }
  }

  get required() {
    return (
      this.form.get('name').hasError('required') &&
      this.form.get('name').touched
    );
  }

  createWorkout() {
    if (this.form.valid) {
      this.create.emit(this.form.value);
    }
  }

  updateWorkout() {
    if (this.form.valid) {
      const workout: WorkoutModel = {
        uuid: this.workout.uuid,
        name: this.form.get('name').value,
        endurance: this.form.get('endurance').value,
        strength: this.form.get('strength').value,
        type: this.form.get('type').value
      };
      this.update.emit(workout);
    }
  }

  removeWorkout() {
    this.remove.emit(this.workout);
  }

  toggle() {
    this.toggled = !this.toggled;
  }
}
