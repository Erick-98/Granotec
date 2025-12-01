import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedTable } from './shared-table';

describe('SharedTable', () => {
  let component: SharedTable;
  let fixture: ComponentFixture<SharedTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SharedTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SharedTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
