import { FormControl } from '@angular/forms';
import { Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { filter, startWith, takeUntil } from 'rxjs/operators';
import { MatAutocomplete, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { IIdentifiable } from '@qro/shared/util-data-access';
import { cloneDeep } from 'lodash';

@Component({
  selector: 'qro-multiple-autocomplete',
  templateUrl: './multiple-autocomplete.component.html',
  styleUrls: ['./multiple-autocomplete.component.scss'],
})
export class MultipleAutocompleteComponent implements OnInit, OnDestroy {
  @Input() nameProperties: string[];
  @Input() control: FormControl;
  @Input() placeholder: string;
  @Input() selectableItems: IIdentifiable[];
  @Output() removed = new EventEmitter<string>();
  @Output() added = new EventEmitter<string>();
  @Output() itemNotFound = new EventEmitter<string>();
  selectedItemIds: string[];
  inputControl = new FormControl();
  separatorKeysCodes: number[] = [ENTER, COMMA];
  @ViewChild('input') input: ElementRef<HTMLInputElement>;
  @ViewChild('auto') autocomplete: MatAutocomplete;
  destroy$: Subject<void> = new Subject<void>();
  filteredList$$: BehaviorSubject<IIdentifiable[]> = new BehaviorSubject<IIdentifiable[]>(undefined);

  ngOnInit() {
    this.filteredList$$.next(this.disabled ? [] : this.selectableItems);
    this.control.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        filter((data) => !!data)
      )
      .subscribe((data: string[]) => {
        this.selectedItemIds = data;
        this.control.markAsDirty();
      });

    if (this.control.value && Array.isArray(this.control.value)) {
      this.selectedItemIds = this.control.value;
    } else {
      console.error('This value is not an Array', this.control.value);
    }

    this.inputControl.valueChanges.pipe(takeUntil(this.destroy$), startWith(null as string)).subscribe((searchTerm) => {
      if (typeof searchTerm === 'string') {
        this._filter(searchTerm);
      }
    });
    this.refreshFilteredList();
  }

  refreshFilteredList(): void {
    this.filteredList$$.next(this.getPrefilteredList());
  }

  resetInput(): void {
    // Clear the input field and reset the autocomplete list
    this.input.nativeElement.value = '';
    this.inputControl.setValue(null);
    this.filteredList$$.next(this.getPrefilteredList());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getPrefilteredList(): IIdentifiable[] {
    let arrayToReturn: IIdentifiable[] = cloneDeep(this.selectableItems);
    this.selectedItemIds.forEach((selectedItem) => {
      arrayToReturn = arrayToReturn.filter((item) => item.id !== selectedItem);
    });
    return arrayToReturn;
  }

  checkInputForData(event: FocusEvent) {
    if (event.relatedTarget && this.autocomplete.panel?.nativeElement?.contains?.(event.relatedTarget)) {
      // Do nothing if the user selected an option from the autocomplete list
      return;
    }
    const input = this.inputControl.value;
    const inList = this.isInputInSelectedList(input);
    if (!inList && input) {
      this.itemNotFound.emit(input);
    }
  }

  private isInputInSelectedList(searchString: string): IIdentifiable {
    return this.selectableItems.find((item) => this.getName(item) === searchString);
  }

  private _filter(searchTerm: string) {
    let arrayToReturn = this.getPrefilteredList().filter((item) => !this.selectedItemIds.includes(item.id));

    if (!searchTerm) {
      this.refreshFilteredList();
    }
    const filterValue = searchTerm.toLowerCase();
    arrayToReturn = arrayToReturn.filter((item) => this.getName(item).toLowerCase().indexOf(filterValue) === 0);
    this.filteredList$$.next(arrayToReturn);
  }

  get disabled(): boolean {
    return this.control.disabled;
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    if (this.disabled) {
      return;
    }
    const selectedValue = event.option.value;
    this.selectedItemIds.push(selectedValue);
    this.setFormControlValue();
    this.added.emit(selectedValue);
    this.refreshFilteredList();
    this.resetInput();
  }

  remove(id: string): void {
    const index = this.selectedItemIds.indexOf(id);

    if (index >= 0) {
      this.selectedItemIds.splice(index, 1);
      this.setFormControlValue();
      this.removed.emit(id);
    }
    this.refreshFilteredList();
  }

  setFormControlValue() {
    this.control.setValue(this.selectedItemIds);
    this.control.markAsDirty();
  }

  getNameById(id: string) {
    const item = this.selectableItems.find((i) => i.id === id);
    return this.getName(item);
  }

  getName(item: IIdentifiable) {
    if (!item) {
      return '';
    }
    let name = '';
    this.nameProperties.forEach((prop) => {
      name += item[prop] + ' ';
    });
    return name.substring(0, name.length - 1);
  }
}
