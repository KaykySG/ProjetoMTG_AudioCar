import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreinfinicoesComponent } from './preinfinicoes.component';

describe('PreinfinicoesComponent', () => {
  let component: PreinfinicoesComponent;
  let fixture: ComponentFixture<PreinfinicoesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreinfinicoesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreinfinicoesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
