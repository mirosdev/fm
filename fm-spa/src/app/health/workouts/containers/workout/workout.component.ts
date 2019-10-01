import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {WorkoutModel, WorkoutsService} from '../../../shared/services/workouts.service';
import {ActivatedRoute, Router} from '@angular/router';
import {switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-workout',
  styleUrls: ['workout.component.scss'],
  templateUrl: 'workout.component.html'
})
export class WorkoutComponent implements OnInit {
  error: string;
  workout$: Observable<any>;
  constructor(private workoutsService: WorkoutsService,
              private router: Router,
              private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.workoutsService.workouts$.subscribe();
    this.workout$ = this.activatedRoute.params
      .pipe(
        switchMap(param => this.workoutsService.getWorkout(param.uuid))
      );
  }

  addWorkout(event: WorkoutModel) {
    this.workoutsService.addWorkout(event).subscribe(newWorkout => {
      this.error = null;
      if (newWorkout) {
        this.router.navigate(['workouts']);
      }
    }, error => {
      this.error = error.error.error ? error.error.error : null;
    });
  }

  updateWorkout(event: WorkoutModel) {
    // console.log('update: ', event);
    this.workoutsService.updateWorkout(event)
      .subscribe(() => {
        this.router.navigate(['workouts']);
      }, () => {
        this.error = 'Internal Server Error';
      });
  }

  removeWorkout(event: WorkoutModel) {
    // console.log('remove: ', event);
    this.workoutsService.removeWorkout(event.uuid)
      .subscribe((deleteSuccess: boolean) => {
        if (deleteSuccess) {
          this.router.navigate(['workouts']);
        } else {
          this.error = 'Delete: This workout doesn\'t exist';
        }
      }, () => {
        this.error = 'Internal Server Error';
      });
  }
}
