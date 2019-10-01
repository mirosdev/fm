import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {MealModel} from '../../services/meals.service';

@Component({
  selector: 'app-list-item',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['list-item.component.scss'],
  templateUrl: 'list-item.component.html'
})
export class ListItemComponent {
  toggled = false;

  @Input() item: MealModel | any;
  @Output() remove = new EventEmitter<MealModel | any>();

  toggle() {
    this.toggled = !this.toggled;
  }

  removeItem() {
    this.remove.emit(this.item);
  }

  getRoute(item: MealModel | any) {
    return [`../${item.ingredients ? 'meals' : 'workouts'}`, item.uuid];
  }
}
