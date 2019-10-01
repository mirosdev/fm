import {Component, OnDestroy, OnInit} from '@angular/core';
import {MealModel, MealsService} from '../../../shared/services/meals.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-meal',
  styleUrls: ['meal.component.scss'],
  templateUrl: 'meal.component.html'
})
export class MealComponent implements OnInit {
  error: string;
  meal$: Observable<any>;
  constructor(private mealsService: MealsService,
              private router: Router,
              private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.mealsService.meals$.subscribe(() => {
      this.meal$ = this.activatedRoute.params
        .pipe(
          switchMap(param => this.mealsService.getMeal(param.uuid))
        );
    });
  }

  addMeal(event: MealModel) {
    this.mealsService.addMeal(event).subscribe(newMeal => {
      this.error = null;
      if (newMeal) {
        this.router.navigate(['meals']);
      }
    }, error => {
        this.error = error.error.error ? error.error.error : null;
    });
  }

  updateMeal(event: MealModel) {
    this.mealsService.updateMeal(event)
      .subscribe(() => {
        this.router.navigate(['meals']);
      }, () => {
        this.error = 'Internal Server Error';
      });
  }

  removeMeal(event: MealModel) {
    this.mealsService.removeMeal(event.uuid)
      .subscribe((deleteSuccess: boolean) => {
        if (deleteSuccess) {
          this.router.navigate(['meals']);
        } else {
          this.error = 'Delete: This meal doesn\'t exist';
        }
      }, () => {
        this.error = 'Internal Server Error';
      });
  }
}
