import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {WorkoutModel, WorkoutsService} from '../../../shared/services/workouts.service';
import {Store} from '../../../../store';

@Component({
  selector: 'app-workouts',
  styleUrls: ['workouts.component.scss'],
  templateUrl: 'workouts.component.html'
})
export class WorkoutsComponent implements OnInit {
  workouts$: Observable<WorkoutModel[]>;
  constructor(private workoutsService: WorkoutsService,
              private store: Store) {}

  ngOnInit(): void {
    this.workouts$ = this.store.select<WorkoutModel[]>('workouts');
    this.workoutsService.workouts$.subscribe();
  }

  removeWorkout(event: WorkoutModel) {
    this.workoutsService.removeWorkout(event.uuid).subscribe();
  }
}
