import {pluck} from 'rxjs/operators';
import {distinctUntilChanged} from 'rxjs/operators';
import {BehaviorSubject, Observable} from 'rxjs';
import {User} from './auth/shared/services/auth/my-auth.service';
import {MealModel} from './health/shared/services/meals.service';
import {WorkoutModel} from './health/shared/services/workouts.service';
import {ScheduleItem} from './health/shared/services/schedule.service';

export interface State {
  user: User;
  meals: MealModel[];
  selected: any;
  list: any;
  schedule: ScheduleItem[];
  date: Date;
  workouts: WorkoutModel[];
  [key: string]: any;
}

const state: State = {
  user: undefined,
  meals: undefined,
  selected: undefined,
  list: undefined,
  schedule: undefined,
  date: undefined,
  workouts: undefined
};

export class Store {

  private subject = new BehaviorSubject<State>(state);
  private store = this.subject.asObservable().pipe(
    distinctUntilChanged());

  get value() {
    return this.subject.value;
  }

  select<T>(name: string): Observable<T> {
    return this.store.pipe(pluck(name));
  }

  set(name: string, stateValue: any) {
    this.subject.next({ ...this.value, [name]: stateValue });
  }

}
