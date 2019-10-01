import {Component, OnInit} from '@angular/core';
import {MealModel, MealsService} from '../../../shared/services/meals.service';
import {Observable} from 'rxjs';
import {Store} from '../../../../store';

@Component({
  selector: 'app-meals',
  styleUrls: ['meals.component.scss'],
  templateUrl: 'meals.component.html'
})
export class MealsComponent implements OnInit {
  meals$: Observable<MealModel[]>;
  constructor(private mealsService: MealsService,
              private store: Store) {}

  ngOnInit(): void {
    this.meals$ = this.store.select<MealModel[]>('meals');
    this.mealsService.meals$.subscribe();
  }

  removeMeal(event: MealModel) {
    this.mealsService.removeMeal(event.uuid).subscribe();
  }
}
