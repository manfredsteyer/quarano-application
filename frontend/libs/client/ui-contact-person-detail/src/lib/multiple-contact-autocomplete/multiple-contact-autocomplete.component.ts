import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { IIdentifiable } from '@qro/shared/util-data-access';
import { MultipleAutocompleteComponent } from '@qro/shared/ui-multiple-autocomplete';
import { ContactDialogService } from '../services/contact-dialog.service';

@Component({
  selector: 'qro-multiple-contact-autocomplete',
  templateUrl: './multiple-contact-autocomplete.component.html',
  styleUrls: ['./multiple-contact-autocomplete.component.scss'],
})
export class MultipleContactAutocompleteComponent {
  @Input() nameProperties: string[];
  @Input() control: FormControl;
  @Input() placeholder: string;
  @Input() selectableItems: IIdentifiable[];
  @Output() removed = new EventEmitter<string>();
  @Output() added = new EventEmitter<string>();
  @Output() itemNotFound = new EventEmitter<string>();

  @ViewChild('contactMultipleAutoComplete')
  contactMultipleAutocomplete: MultipleAutocompleteComponent;

  constructor(private dialogService: ContactDialogService) {}

  addMissingContactPerson(name: string) {
    this.dialogService.askAndOpenContactPersonDialog(name).subscribe((createdContact) => {
      this.contactMultipleAutocomplete.resetInput();
      if (createdContact) {
        if (createdContact.id) {
          this.emitAdded(createdContact.id);
        }
        this.contactMultipleAutocomplete.resetInput();
        this.selectableItems.push(createdContact);
        this.control.patchValue([...this.control.value, createdContact.id]);
      }
    });
  }

  emitItemNotFound(event: any) {
    this.itemNotFound.emit(event);
  }

  emitAdded(event: any) {
    this.added.emit(event);
  }

  emitRemoved(event: any) {
    this.removed.emit(event);
  }
}
