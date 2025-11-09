import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { Client, UpdateClient } from '../../models/client';
import { finalize, tap } from 'rxjs/operators';
import { Component, OnInit } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ConfirmationService } from 'primeng/api';
  loading = true;
  account?: Client;
  errorMessage?: string;
  formState: UpdateClient = {};
  constructor(
    private clientService: ClientService,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private cdr: ChangeDetectorRef
  ) {}
  carregarDados(): void {
    console.log('Iniciando carregarDados');
    this.loading = true;
    this.errorMessage = undefined;
    this.clientService.getAcc()
      .pipe(
        tap({
          next: (data) => console.log('Conta carregada com sucesso', data),
          error: (error) => console.error('Falha ao requisitar conta', error)
        }),
        finalize(() => {
          this.loading = false;
          console.log('Finalizando carregarDados');
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          this.account = data;
          this.syncFormState();
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = this.buildErrorMessage(error);
          this.messageService.add({
            severity: 'error',
            summary: 'Erro ao carregar dados',
            detail: this.errorMessage
          });
          this.cdr.detectChanges();
        }
      });
  }
  private onlyDigits(value: string): string {
    return value.replace(/\D/g, '');
  }

  private buildErrorMessage(error: any): string {
    if (!error) {
      return 'Erro desconhecido ao acessar seus dados.';
    }
    if (error.status === 401 || error.status === 403) {
      return 'Sessão expirada ou acesso não autorizado. Faça login novamente.';
    }
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    return error.error?.message || 'Não foi possível carregar seus dados no momento.';
  }
