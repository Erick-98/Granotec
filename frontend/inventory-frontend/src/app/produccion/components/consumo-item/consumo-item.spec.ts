import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsumoItem } from './consumo-item';

describe('ConsumoItem', () => {
  let component: ConsumoItem;
  let fixture: ComponentFixture<ConsumoItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConsumoItem]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsumoItem);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
