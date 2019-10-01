import {Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Observable, of} from 'rxjs';
import {filter, map, tap} from 'rxjs/operators';
import {Store} from '../../../store';
import {HttpClient} from '@angular/common/http';

interface StrengthModel {
  reps: number;
  sets: number;
  weight: number;
}

interface EnduranceModel {
  distance: number;
  duration: number;
}

export interface WorkoutModel {
  name: string;
  type: string;
  strength: StrengthModel;
  endurance: EnduranceModel;
  uuid: string;
}

@Injectable()
export class WorkoutsService {
  private apiHostUrl = environment.apiHostUrl;
  private workoutsResource = '/api/workouts';

  workouts$: Observable<WorkoutModel[]> = this.httpClient.get<WorkoutModel[]>(this.apiHostUrl + this.workoutsResource + '/all')
    .pipe(
      tap(workouts => {
        this.store.set('workouts', workouts);
      }, () => {
        this.store.set('workouts', []);
      })
    );

  constructor(private store: Store,
              private httpClient: HttpClient) {}

  getWorkout(uuid: string) {
    if (!uuid) {
      return of({});
    }
    return this.store.select<WorkoutModel[]>('workouts')
      .pipe(
        filter(Boolean),
        map((workouts: WorkoutModel[]) => workouts.find((workout: WorkoutModel) => workout.uuid === uuid))
      );
  }

  addWorkout(workout: WorkoutModel) {
    return this.httpClient.post<WorkoutModel>(this.apiHostUrl + this.workoutsResource + '/add', workout)
      .pipe(
        tap((savedWorkout: WorkoutModel) => {
          const workouts: WorkoutModel[] = this.store.value.workouts;
          workouts.push(savedWorkout);
          this.store.set('workouts', workouts);
        })
      );
  }

  // HANDLE STATUS ERRORS
  updateWorkout(workout: WorkoutModel) {
    return this.httpClient.put<WorkoutModel>(this.apiHostUrl + this.workoutsResource + '/update', workout)
      .pipe(
        tap((updatedWorkout: WorkoutModel) => {
          const workouts: WorkoutModel[] = this.store.value.workouts;
          const oldVerWorkout = workouts.find(storeWorkout => storeWorkout.uuid === updatedWorkout.uuid);
          workouts.splice(workouts.indexOf(oldVerWorkout), 1, updatedWorkout);
          this.store.set('workouts', workouts);
        })
      );
  }

  // HANDLE ERRORS AND -> success === false
  removeWorkout(uuid: string) {
    return this.httpClient.delete<boolean>(this.apiHostUrl + this.workoutsResource + '/delete/' + uuid)
      .pipe(
        tap((deleteSuccess: boolean) => {
          if (deleteSuccess) {
            const workouts: WorkoutModel[] = this.store.value.workouts;
            const deletedWorkout: WorkoutModel = workouts.find(workout => workout.uuid === uuid);
            if (deletedWorkout) {
              workouts.splice(workouts.indexOf(deletedWorkout), 1);
              this.store.set('workouts', workouts);
            }
          }
        })
      );
  }
}
