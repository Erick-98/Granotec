import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdenCard } from './orden-card';

describe('OrdenCard', () => {
  let component: OrdenCard;
  let fixture: ComponentFixture<OrdenCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrdenCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrdenCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
