import {Component, OnDestroy, OnInit} from '@angular/core';
import {Observable, Subscription} from 'rxjs';
import {ScheduleItem, ScheduleService} from '../../../shared/services/schedule.service';
import {Store} from '../../../../store';
import {MealModel, MealsService} from '../../../shared/services/meals.service';
import {WorkoutModel, WorkoutsService} from '../../../shared/services/workouts.service';

@Component({
  selector: 'app-schedule',
  styleUrls: ['schedule.component.scss'],
  templateUrl: 'schedule.component.html'
})
export class ScheduleComponent implements OnInit, OnDestroy {
  open = false;
  date$: Observable<Date>;
  selected$: Observable<any>;
  list$: Observable<MealModel[] | WorkoutModel[]>;
  schedule$: Observable<ScheduleItem[]>;
  subscriptions: Subscription[] = [];
  constructor(private scheduleService: ScheduleService,
              private store: Store,
              private mealsService: MealsService,
              private workoutsService: WorkoutsService) {}

  changeDate(date: Date) {
    this.scheduleService.updateDate(date);
  }

  changeSection(event: any) {
    this.open = true;
    this.scheduleService.selectSection(event);
  }

  ngOnInit(): void {
    this.date$ = this.store.select('date');
    this.schedule$ = this.store.select('schedule');
    this.selected$ = this.store.select('selected');
    this.list$ = this.store.select('list');
    this.subscriptions = [
      this.scheduleService.schedule$.subscribe(),
      this.scheduleService.selected$.subscribe(),
      this.scheduleService.list$.subscribe(),
      this.scheduleService.items$.subscribe(() => {
        this.scheduleService.refetchSchedules();
      }),
      this.mealsService.meals$.subscribe(),
      this.workoutsService.workouts$.subscribe()
    ];
  }

  ngOnDestroy(): void {
    if (this.subscriptions.length > 0) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
    }
  }

  assignItem(items: any) {
    this.scheduleService.updateItems(items);
    this.closeAssign();
  }

  closeAssign() {
    this.open = false;
  }
}
