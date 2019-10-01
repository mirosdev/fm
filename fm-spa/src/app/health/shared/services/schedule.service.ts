import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of, Subject} from 'rxjs';
import {Store} from '../../../store';
import {map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {MealModel} from './meals.service';
import {WorkoutModel} from './workouts.service';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';

export interface ScheduleItem {
  meals: MealModel[];
  workouts: WorkoutModel[];
  section: string;
  timestamp: number;
  uuid?: string;
}

export interface ScheduleList {
  morning?: ScheduleItem;
  lunch?: ScheduleItem;
  evening?: ScheduleItem;
  snacks?: ScheduleItem;
  [key: string]: any;
}

@Injectable()
export class ScheduleService {
  private apiHostUrl = environment.apiHostUrl;
  private scheduleResource = '/api/schedule';
  private date$ = new BehaviorSubject(new Date());
  private section$ = new Subject();
  private itemList$ = new Subject();

  items$ = this.itemList$.pipe(
    withLatestFrom(this.section$),
    map(([items, section]: any) => {
      const uuid = section.data.uuid;
      const defaults: ScheduleItem = {
        workouts: null,
        meals: null,
        section: section.section,
        timestamp: new Date(section.day).getTime()
      };

      let payload = null;
      if (section.type === 'meals') {
        payload = {
          ...(uuid ? section.data : defaults),
          ...(items.meals && items.meals.length > 0 ? items : {meals: null})
        };
      } else if (section.type === 'workouts') {
        payload = {
          ...(uuid ? section.data : defaults),
          ...(items.workouts && items.workouts.length > 0 ? items : {workouts: null})
        };
      }

      if (uuid) {
        return {uuid, payload};
      } else {
        return {uuid: null, payload};
      }
    }),
    switchMap((next: any) => {
      if (next.uuid && next.payload) {
        return this.updateSection(next.uuid, next.payload);
      } else if (next.payload) {
        return this.createSection(next.payload);
      } else if (!next) {
        return of(null);
      }
    })
  );

  selected$ = this.section$
    .pipe(
      tap((next: any) => this.store.set('selected', next))
    );

  list$ = this.section$
    .pipe(
      map((value: any) => this.store.value[value.type]),
      tap((next: any) => this.store.set('list', next))
    );

  schedule$: Observable<ScheduleItem[]> = this.date$
    .pipe(
      tap((date: any) => this.store.set('date', date)),
      map((day: any) => {
        const startAt = (
          new Date(day.getFullYear(), day.getMonth(), day.getDate())
        ).getTime();

        const endAt = (
          new Date(day.getFullYear(), day.getMonth(), day.getDate() + 1)
        ).getTime() - 1;

        return { startAt, endAt };
      }),
      switchMap(({ startAt, endAt }: any) => this.getSchedule(startAt, endAt)),
      map((data: any[]) => {
        const mapped: ScheduleList = {};
        data.forEach(prop => {
          if (!mapped[prop.section]) {
            mapped[prop.section] = prop;
          }
        });
        return mapped;
      }),
      tap((next: any) => this.store.set('schedule', next))
    );
  constructor(private store: Store,
              private httpClient: HttpClient) {}

  updateItems(items: string[]) {
    this.itemList$.next(items);
  }

  updateDate(date: Date) {
    this.date$.next(date);
  }

  refetchSchedules() {
    this.date$.next(this.date$.value);
  }

  selectSection(event: any) {
    this.section$.next(event);
  }

  private createSection(payload: ScheduleItem) {
    return this.httpClient.post(this.apiHostUrl + this.scheduleResource + '/create', payload);
  }

  private updateSection(uuid: string, payload: ScheduleItem) {
    return this.httpClient.put(this.apiHostUrl + this.scheduleResource + '/update/' + uuid, payload);
  }

  private getSchedule(startAt: number, endAt: number) {
    return this.httpClient.get(this.apiHostUrl + this.scheduleResource + '/' + startAt + '/' + endAt);
  }
}
