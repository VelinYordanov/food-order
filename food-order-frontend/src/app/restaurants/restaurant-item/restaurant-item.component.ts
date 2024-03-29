import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RestaurantListItem } from '../models/restaurant-list-item';

@Component({
  selector: 'app-restaurant-item',
  templateUrl: './restaurant-item.component.html',
  styleUrls: ['./restaurant-item.component.scss']
})
export class RestaurantItemComponent implements OnInit {
  @Input('restaurant') restaurant: RestaurantListItem;
  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  goToDetails() {
    this.router.navigate(['restaurants', this.restaurant.id]);
  }
}
