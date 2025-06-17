import { TestBed } from '@angular/core/testing';

import { BalancoAudioService } from './balanco-audio.service';

describe('BalancoAudioService', () => {
  let service: BalancoAudioService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BalancoAudioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
