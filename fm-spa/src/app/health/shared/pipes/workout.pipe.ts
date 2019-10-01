import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'workout'
})
export class WorkoutPipe implements PipeTransform {
  transform(value: any) {
    if (value.type === 'endurance') {
      if (value.endurance.distance && value.endurance.duration) {
        return `Distance: ${value.endurance.distance + 'km'}, Duration: ${value.endurance.duration + 'mins'}`;
      } else {
        return;
      }
    } else {
      if (value.strength.weight && value.strength.reps && value.strength.sets) {
        return `Weight: ${value.strength.weight + 'kg'}, Reps: ${value.strength.reps}, Sets: ${value.strength.sets}`;
      } else {
        return;
      }
    }
  }
}
