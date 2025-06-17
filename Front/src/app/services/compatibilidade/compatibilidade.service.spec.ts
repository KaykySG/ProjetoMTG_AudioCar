import { TestBed } from '@angular/core/testing';

import { CompatibilidadeService } from './compatibilidade.service';

describe('CompatibilidadeService', () => {
  let service: CompatibilidadeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CompatibilidadeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
