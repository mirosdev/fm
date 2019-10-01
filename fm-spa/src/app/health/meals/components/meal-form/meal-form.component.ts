import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormArray, FormBuilder, FormControl, Validators} from '@angular/forms';
import {MealModel} from '../../../shared/services/meals.service';

@Component({
  selector: 'app-meal-form',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['meal-form.component.scss'],
  templateUrl: 'meal-form.component.html'
})
export class MealFormComponent implements OnChanges {
  toggled = false;
  exists = false;
  @Output() create = new EventEmitter<MealModel>();
  @Output() update = new EventEmitter<MealModel>();
  @Output() remove = new EventEmitter<MealModel>();
  @Input() meal: MealModel;
  @Input() error: string;

  form = this.fb.group({
    name: ['', Validators.required],
    ingredients: this.fb.array([''])
  });

  constructor(private fb: FormBuilder) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (this.meal && this.meal.name) {
      this.exists = true;
      this.emptyIngredients();

      const value = this.meal;
      this.form.patchValue(value);

      if (value.ingredients) {
        value.ingredients
          .forEach(ingredient => {
            this.ingredients.push(new FormControl(ingredient));
          });
      }
    }
  }

  emptyIngredients() {
    while (this.ingredients.controls.length) {
      this.ingredients.removeAt(0);
    }
  }

  get required() {
    return (
      this.form.get('name').hasError('required') &&
      this.form.get('name').touched
    );
  }

  get ingredients() {
    return this.form.get('ingredients') as FormArray;
  }

  addIngredient() {
    this.ingredients.push(new FormControl(''));
  }

  removeIngredient(index: number) {
    this.ingredients.removeAt(index);
  }

  createMeal() {
    if (this.form.valid) {
      this.create.emit(this.form.value);
    }
  }

  updateMeal() {
    if (this.form.valid) {
      const meal: MealModel = {
        uuid: this.meal.uuid,
        name: this.form.get('name').value,
        ingredients: this.form.get('ingredients').value
      };
      this.update.emit(meal);
    }
  }

  removeMeal() {
    this.remove.emit(this.meal);
  }

  toggle() {
    this.toggled = !this.toggled;
  }
}
