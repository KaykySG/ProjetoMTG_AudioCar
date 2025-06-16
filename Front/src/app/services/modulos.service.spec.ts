import { TestBed } from '@angular/core/testing';

import { modulosService } from './modulos.service';

describe('modulosServiceService', () => {
  let service: modulosService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(modulosService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
