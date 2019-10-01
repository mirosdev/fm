import {Injectable} from '@angular/core';
import {Store} from '../../../store';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {filter, map, tap} from 'rxjs/operators';

export interface MealModel {
  name: string;
  ingredients: string[];
  uuid: string;
}

@Injectable()
export class MealsService {
  private apiHostUrl = environment.apiHostUrl;
  private mealsResource = '/api/meals';

  meals$: Observable<MealModel[]> = this.httpClient.get<MealModel[]>(this.apiHostUrl + this.mealsResource + '/all')
    .pipe(
      tap(meals => {
        this.store.set('meals', meals);
      }, () => {
        this.store.set('meals', []);
      })
    );

  constructor(private store: Store,
              private httpClient: HttpClient) {}

  getMeal(uuid: string) {
    if (!uuid) {
      return of({});
    }
    return this.store.select<MealModel[]>('meals')
      .pipe(
        filter(Boolean),
        map((meals: MealModel[]) => meals.find((meal: MealModel) => meal.uuid === uuid))
      );
  }

  addMeal(meal: MealModel) {
    return this.httpClient.post<MealModel>(this.apiHostUrl + this.mealsResource + '/add', meal)
      .pipe(
        tap((savedMeal: MealModel) => {
          const meals: MealModel[] = this.store.value.meals;
          meals.push(savedMeal);
          this.store.set('meals', meals);
        })
      );
  }

  // HANDLE STATUS ERRORS
  updateMeal(meal: MealModel) {
    return this.httpClient.put<MealModel>(this.apiHostUrl + this.mealsResource + '/update', meal)
      .pipe(
        tap((updatedMeal: MealModel) => {
          const meals: MealModel[] = this.store.value.meals;
          const oldVerMeal = meals.find(storeMeal => storeMeal.uuid === updatedMeal.uuid);
          meals.splice(meals.indexOf(oldVerMeal), 1, updatedMeal);
          this.store.set('meals', meals);
        })
      );
  }

  // HANDLE ERRORS AND -> success === false
  removeMeal(uuid: string) {
    return this.httpClient.delete<boolean>(this.apiHostUrl + this.mealsResource + '/delete/' + uuid)
      .pipe(
        tap((deleteSuccess: boolean) => {
          if (deleteSuccess) {
            const meals: MealModel[] = this.store.value.meals;
            const deletedMeal: MealModel = meals.find(meal => meal.uuid === uuid);
            if (deletedMeal) {
              meals.splice(meals.indexOf(deletedMeal), 1);
              this.store.set('meals', meals);
            }
          }
        })
      );
  }
}
